package flutterby.core

package object errorhandler {
  type ErrorHandler = Context => `IsHandled?`
}

package errorhandler {

  object ErrorHandler {
    import org.flywaydb.core.api.errorhandler.{ ErrorHandler => FlywayErrorHandler, Context => FlywayContext }
    def toFlyway(e: ErrorHandler): FlywayErrorHandler =
      (context: FlywayContext) => e(Context.fromFlyway(context)).isHandled
  }

  import flutterby.core.jdk.CollectionConversions

  sealed trait `IsHandled?`
  object `IsHandled?` {
    case object YesHanded extends `IsHandled?`
    case object NotHandled extends `IsHandled?`

    def isHandled(i: `IsHandled?`): Boolean = i match {
      case YesHanded  => true
      case NotHandled => false
    }

    implicit class `IsHandled?Ops`(val i: `IsHandled?`) extends AnyVal {
      def isHandled = `IsHandled?`.isHandled(i)
    }

  }

  final case class ErrorHandlers(errorHandlers: Vector[ErrorHandler])

  final case class ErrorCode(value: Int) extends AnyVal
  final case class ErrorState(value: String) extends AnyVal
  final case class ErrorMessage(value: String) extends AnyVal

  final case class Error(code: ErrorCode, state: ErrorState, message: ErrorMessage)
  object Error {
    import org.flywaydb.core.api.errorhandler.{ Error => FlywayError }
    def fromFlyway(e: FlywayError): Error =
      Error(ErrorCode(e.getCode), ErrorState(e.getState), ErrorMessage(e.getMessage))
  }

  final case class WarningCode(value: Int) extends AnyVal
  final case class WarningState(value: String) extends AnyVal
  final case class WarningMessage(value: String) extends AnyVal

  final case class Warning(code: WarningCode, state: WarningState, message: WarningMessage)
  object Warning {
    import org.flywaydb.core.api.errorhandler.{ Warning => FlywayWarning }
    def fromFlyway(w: FlywayWarning): Warning =
      Warning(WarningCode(w.getCode), WarningState(w.getState), WarningMessage(w.getMessage))
  }

  final case class Context(warnings: List[Warning], errors: List[Error])
  object Context {
    import org.flywaydb.core.api.errorhandler.{ Context => FlywayContext }
    def fromFlyway(c: FlywayContext): Context = {
      val warnings = CollectionConversions.toScalaList(c.getWarnings).map(Warning.fromFlyway)
      val errors   = CollectionConversions.toScalaList(c.getErrors).map(Error.fromFlyway)
      Context(warnings, errors)
    }
  }

}
