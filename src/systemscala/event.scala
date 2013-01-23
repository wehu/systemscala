package systemscala

class Event(n: String = "", tmp: Boolean = false) {
  type CB = ()=>Unit
  var cbs = scala.collection.mutable.HashMap[CB, CB]()
  Event.counter += 1
  val name = if (n == "") "event " + Event.counter else n
  if(!tmp)
    Event.add(this)
  override def toString() = name
  def subscribe(cb: => Unit) : CB = {
    val _cb = ()=>{cb}
    cbs += (_cb -> _cb)
    _cb
  }
  def _notify {
    cbs foreach ((p)=>{p._1()}) 
  }
  def remove (cb: CB) {
    cbs -= cb
  }
  def tmpEvent: Event = {
    val e = new Event("", tmp)
    var id: CB = null
    id = this.subscribe{
      this.remove(id)
      e._notify
    }
    e
  }
  def or(o: Event, tmp: Boolean = true): Event = {
    val e = new Event("", tmp)
    var t: Int = -1
    var id0: CB = null
    var id1: CB = null
    val cb = ()=>{
      if (tmp) {
        this.remove(id0)
        o.remove(id1)
      }
      if (t != SimTime.timeline)
        e._notify
      t = SimTime.timeline
    }
    id0 = this.subscribe {cb()}
    id1 = o.subscribe {cb()}
    e
  }
  def and(o: Event, tmp: Boolean = true): Event = {
    val e = new Event("", tmp)
    var t: Int = -1
    var id0: CB = null
    var id1: CB = null
    val cb = ()=>{
      if (t == SimTime.timeline){
        if (tmp) {
          this.remove(id0)
          o.remove(id1)
        }
        e._notify
      }
      t = SimTime.timeline
    }
    id0 = this.subscribe {cb()}
    id1 = o.subscribe {cb()}
    e
  }
}

object Event {
  var counter = 0
  var events = scala.collection.mutable.HashMap[String, Event]()
  def apply(name: String = "") : Event = {
    new Event(name)
  }
  def add(e: Event){
    events.get(e.name) match {
      case Some(_) => throw new Exception("Attempt to redefine event " + e.name)
      case _ => events += (e.name -> e) 
    }
  }
  def event(n: String): Event = {
    events.get(n) match {
      case None => throw new Exception("Cannot find event " + n)
      case Some(e) => e
    }
  }
  def remove(n: String) {
    events -= n
  }
  def remove(e: Event) {
    events -= e.name
  }
}
