package systemscala

class Pipe(val name: String, val parent: Component = Component.root) {
  import scala.util.continuations.cps
  val fullname: String = (if (parent == null) ""  else parent.fullname + ".") + name
  var pipe = scala.collection.mutable.Queue[Any]()
  Pipe.add(this)
  Event(fullname + "." + "read")
  Event(fullname + "." + "write")
  Event(fullname + "." + "changed")
  def event(n: String): Event = {
    Event.event(fullname + "." + n)
  }
  def onRead = event("read")
  def onWrite = event("write")
  def read: Any@cps[Unit] = {
    onRead._notify
    if(pipe.isEmpty){
      onWrite._wait()
      pipe.dequeue
    } else {
      pipe.dequeue
    }
  }
  def write(v: Any): Unit = {
    onWrite._notify
    pipe += v
  }
}

object Pipe{
  var insts = scala.collection.mutable.HashMap[String, Pipe]()
  def apply(name: String, parent: Component = Component.root) : Pipe ={
    new Pipe(name, parent)
  }
  def add[T](s: Pipe){
    insts += (s.fullname -> s)
  }
  def pipe(n: String): Pipe = {
    insts.get(n) match {
      case None => throw new Exception("Cannot find Pipe " + n)
      case Some(p) => p
    }
  }
}