package systemscala

object helper {
  def root = Component.root
  val event = Event.event(_)
  val pipe = Pipe.pipe(_)
  val signal = Signal.signal(_)
  //val delta = SimTime.delta(_)
  val run = Simulate.run(_)
  def stop = Simulate.stop 
  def sleep = Thread.sleep
  val wake = Thread.wake(_)
  def info(args: Any*) = Logger.info(args.mkString)
  def debug(args: Any*) = Logger.debug(args.mkString)
  def error(args: Any*) = Logger.error(args.mkString)
  def warn(args: Any*) = Logger.warn(args.mkString)
}