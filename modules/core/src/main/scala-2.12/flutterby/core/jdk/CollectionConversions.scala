package flutterby.core.jdk

import java.util

object CollectionConversions {
  import scala.collection.JavaConverters._
  def toJavaMap[A, B](m: Map[A, B]): util.Map[A, B] = m.asJava
}