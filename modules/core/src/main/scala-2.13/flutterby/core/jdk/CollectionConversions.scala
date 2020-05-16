package flutterby.core.jdk

import java.util

object CollectionConversions {

  import scala.jdk.CollectionConverters._

  def toScalaMap[A, B](m: util.Map[A, B]): Map[A, B] = m.asScala.toMap

  def toJavaMap[A, B](m: Map[A, B]): util.Map[A, B] = m.asJava

  def toScalaList[A](l: util.List[A]): List[A] = l.asScala.toList

  def toJavaCollection[A](i: Iterable[A]): util.Collection[A] = i.toList.asJava
}
