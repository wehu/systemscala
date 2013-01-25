package systemscala

object Helper {
  import scala.util.continuations.cps
  import SignalMgr._
  import PipeMgr._
  def root = Component.root
  val event = Event.event(_)
  val component = Component.component(_)
  def pipe[T](name:String)(implicit pm: PipeMgr[T]) = Pipe.pipe[T](name)(pm)
  def signal[T](name:String)(implicit sm: SignalMgr[T]) = Signal.signal[T](name)(sm)
  //val delta = SimTime.delta(_)
  val run = Simulate.run(_)
  def stop = Simulate.stop 
  def sleep = Thread.sleep
  val wake = Thread.wake(_)
  def info(args: Any*) = Logger.info(args.mkString)
  def debug(args: Any*) = Logger.debug(args.mkString)
  def error(args: Any*) = Logger.error(args.mkString)
  def warn(args: Any*) = Logger.warn(args.mkString)
  def spawn(b: =>Unit@cps[Unit], n:String = "") = Thread.spawn(b, n)
  def join(ts: Thread*) = Thread.join(ts: _*)
}