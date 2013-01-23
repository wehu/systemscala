package systemscala

class Signal(val name: String, val parent: Component = Component.root, var oldVal: Any = null) {
  val fullname: String = (if (parent == null) ""  else parent.fullname + ".") + name
  var newVal = oldVal
  Signal.add(this)
  Event(fullname + "." + "read")
  Event(fullname + "." + "write")
  Event(fullname + "." + "changed")
  def event(n: String): Event = {
    Event.event(fullname + "." + n)
  }
  def read: Any = {
    event("read")._notify
    newVal
  }
  def write(v: Any): Unit = {
    event("write")._notify
    if(oldVal != v)
      event("changed")._notify
    newVal = v
  }
  def sync {
    oldVal = newVal
  }
}

object Signal{
  var insts = scala.collection.mutable.HashMap[String, Signal]()
  def add[T](s: Signal){
    insts += (s.fullname -> s)
  }
  def sync {
    for((n, s) <- insts){
      s.sync
    }
  }
}

