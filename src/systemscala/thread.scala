package systemscala
import scala.util.continuations._

class Thread(var body: ()=>Unit @cps[Unit], n: String = "") {
  Thread.counter += 1
  val name = if (n == "") ("thread " + Thread.counter) else n
  Thread.add(this)
  val parent = if (name == "root") this else Thread.current
  override def toString() = name
  def resume: Unit @cps[Unit] = {
    Thread.current = this
    body()
  }
  def _yield: Unit @cps[Unit] = {
    shift { k: ( Unit => Unit) => {
        body = ()=>{k()}
      }
    }
  }
}

object Thread {
  var counter = 0
  var root : Thread = null
  var current = root
  var queue_r = scala.collection.mutable.HashMap[Thread, Thread]()
  var queue_s = scala.collection.mutable.HashMap[Thread, Thread]()
  def remove(t: Thread){
    queue_r -= t
    queue_s -= t
  }
  def add(t: Thread){
    queue_r += (t -> t)
  }
  def apply(body: => Unit @cps[Unit], name: String = ""): Thread = {
    new Thread(()=>{body}, name)
  }
  def sleep: Unit @cps[Unit] = {
    var c = Thread.current
    queue_r -= c
    queue_s += (c -> c)
    c._yield
  }
  def wake(t: Thread){
    queue_s -= t
    queue_r += (t -> t)
  }
  def run(body: => Unit @cps[Unit]) {
    root = this(body, "root")
    runOne
  }
  def runOne {
    var rs = queue_r.keys
    if (rs.size > 0){
      for (r <- rs) {
        Thread.current = root
        remove(r)
        reset {
          r.resume
        }
      }
      runOne
    }
  }
}
