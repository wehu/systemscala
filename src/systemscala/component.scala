package systemscala

class Component(val name: String, val parent: Component = Component.root) {
  import scala.util.continuations._
  val fullname: String = (if (parent == null) ""  else parent.fullname + ".") + name
  Component.add(this)
  def build = {}
  def connect = {}
  def run : Unit@cps[Unit] = {}
  var threads = scala.collection.mutable.Queue[()=>Unit@cps[Unit]]()
  def wait(e: Event) : Unit@cps[Unit] = {
    val te = e.tmpEvent
    val c = Thread.current
    te.subscribe {
      Thread.wake(c)
    }
    Thread.sleep
  }
  def addThread(body: =>Unit@cps[Unit]) {
    threads += (()=>{body})
  }
  def initial(body: =>Unit@cps[Unit]) = addThread{body}
  def always(e: => Event)(body: =>Unit@cps[Unit]) {
    val cb: ()=>Unit@cps[Unit] = ()=>{
      while(true){
        wait(e)
        body
      }
    }
    threads += cb
  }
  def initThreads {
    for(t <- threads){
      Thread {t()}
    }
  }
}

object Component {
  val root: Component = null
  var insts = scala.collection.mutable.HashMap[String, Component]()
  def add(c: Component){
    insts += (c.fullname -> c)
  }
  def run {
    for ((n, i) <- insts) {
      i.build
    }
    for ((n, i) <- insts) {
      i.connect
    }
    for ((n, i) <- insts) {
      i.initThreads
    }
    for ((n, i) <- insts) {
      Thread {i.run}
    }
  }
}