package systemscala

object Helper {
  import scala.util.continuations.cps
  def root = Component.root
  val event = Event.event(_)
  val component = Component.component(_)
  def pipe[T: Manifest](name:String) = Pipe.pipe[T](name)
  def signal[T: Manifest](name:String) = Signal.signal[T](name)
  //val delta = SimTime.delta(_)
  val run = Simulate.run(_)
  def stop = Simulate.stop 
  def sleep = Thread.sleep
  val wake = Thread.wake(_)
  def info(args: Any*) = Logger.info(args: _*)
  def debug(args: Any*) = Logger.debug(args: _*)
  def error(args: Any*) = Logger.error(args: _*)
  def warn(args: Any*) = Logger.warn(args: _*)
  def spawn(b: =>Unit@cps[Unit], n:String = "") = Thread.spawn(b, n)
  def join(ts: Thread*) = Thread.join(ts: _*)
}