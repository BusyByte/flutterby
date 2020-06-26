package flutterby.cats

import java.time.Duration
import java.time.temporal.ChronoUnit.SECONDS

import cats.effect.IO
import com.dimafeng.testcontainers.{Container, GenericContainer}
import flutterby.core.Flutterby
import org.specs2.matcher.MatchResult
import org.specs2.mutable.Specification
import org.specs2.specification.{BeforeAfterAll, BeforeAfterEach}
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy
import cats.implicits._
import flutterby.cats.config.ConfigBuilder

trait ForAllTestContainer extends BeforeAfterAll {

  def container: Container

  override def beforeAll(): Unit = {
    container.start()
    afterStart()
  }

  override def afterAll(): Unit = {
    beforeStop()
    container.stop()
  }

  def afterStart(): Unit = {}

  def beforeStop(): Unit = {}

}

class FlutterbyCatsSpec extends Specification with ForAllTestContainer with BeforeAfterEach {
  sequential

  lazy val dbUserName = "TestUser"
  lazy val dbPassword = "password"
  lazy val dbName     = "test_db"
  lazy val dbPort     = 5432

  override lazy val container = GenericContainer(
    "christopherdavenport/postgres-multi-db:10.3",
    exposedPorts = Seq(dbPort),
    env = Map(
      "REPO"                        -> "https://github.com/mrts/docker-postgresql-multiple-databases",
      "POSTGRES_USER"               -> dbUserName,
      "POSTGRES_PASSWORD"           -> dbPassword,
      "POSTGRES_MULTIPLE_DATABASES" -> dbName
    ),
    waitStrategy = new LogMessageWaitStrategy()
      .withRegEx(".*database system is ready to accept connections.*\\s")
      .withTimes(2)
      .withStartupTimeout(Duration.of(60, SECONDS))
  )
  lazy val driverName         = "org.postgresql.Driver"
  lazy val jdbcUrl            =
    s"jdbc:postgresql://${container.container.getContainerIpAddress}:${container.container.getMappedPort(dbPort)}/$dbName"

  import syntax.all._
  lazy val flutterby: IO[Flutterby[IO]] =
    ConfigBuilder
      .impl[IO]
      .dataSource(jdbcUrl, dbUserName, dbPassword)
      .load

  lazy val dbClean: IO[Unit] = for {
    fb <- flutterby
    _  <- fb.clean()
  } yield ()

  override protected def before: Any =
    dbClean.unsafeRunSync()

  override protected def after: Any = {}

  "happy path" in {
    val result: IO[MatchResult[Any]] = for {
      fb                                <- flutterby
      validateResultBeforeMigrate       <- fb.validate().attempt
      infoBeforeMigrate                 <- fb.info()
      _                                 <- fb.baseline()
      successfullyAppliedMigrationCount <- fb.migrate()
      _                                 <- fb.baseline()
      _                                 <- fb.validate()
      infoAfterMigrate                  <- fb.info()
    } yield {
      validateResultBeforeMigrate.leftMap(_.getMessage) aka "validateResultBeforeMigrate" must beLeft.which {
        case msg: String =>
          msg must contain("Validate failed:")
          msg must contain("Detected resolved migration not applied to database: 1")
          msg must contain("Detected resolved migration not applied to database: 2")
      }

      infoBeforeMigrate.all aka "allMigrationsBeforeMigrate" must haveSize(2)
      infoBeforeMigrate.pending aka "pendingMigrationsBeforeMigrate" must haveSize(2)
      infoBeforeMigrate.current aka "currentMigrationBeforeMigrate" must beNone
      infoBeforeMigrate.applied aka "appliedMigrationsBeforeMigrate" must haveSize(0)

      successfullyAppliedMigrationCount aka "successfullyAppliedMigrationCount" must_== 1

      infoAfterMigrate.all aka "allMigrationsAfterMigrate" must haveSize(2)
      infoAfterMigrate.pending aka "pendingMigrationsAfterMigrate" must haveSize(0)
      infoAfterMigrate.current.flatMap(_.version.version) aka "currentMigrationAfterMigrate" must beSome("2")
      infoAfterMigrate.applied aka "appliedMigrationsAfterMigrate" must haveSize(2)
    }

    result.unsafeRunSync()
  }

}
