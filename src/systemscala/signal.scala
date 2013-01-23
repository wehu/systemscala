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
  def onRead = event("read")
  def onWrite = event("write")
  def onChanged = event("changed")
  def read: Any = {
    onRead._notify
    newVal
  }
  def write(v: Any): Unit = {
    onWrite._notify
    if(newVal != v){
      newVal = v
      onChanged._notify
    }
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

