package systemscala

class SimTime(val delay: Int = 0) extends Event {
  val simtime = SimTime.timeline + delay * SimTime.timescale
  SimTime.add(this)
  override def toString() = "Simulation time " + simtime
  override def _notify {
    super._notify
    Event.remove(this)
  }
}

object SimTime {
  type Queue = scala.collection.mutable.Queue[SimTime]
  var timeline = 0
  var timescale = 1
  var sts = scala.collection.mutable.HashMap[Int, Queue]()

  def apply(delay: Int = 0) : SimTime = {
    new SimTime(delay)
  }
  def add(s: SimTime) {
    sts.get(s.simtime) match {
      case None => var q = new Queue
        q += s
        sts += (s.simtime -> q) 
      case Some(q) => q += s
    }
  }
  def getRecents: Option[Queue] = {
    if (sts.size > 0) {
      val (t, ss) = sts.head
      sts -= t
      timeline = t
      Some(ss)
    } else {
      None
    }
  }
}

