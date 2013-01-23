package systemscala

abstract class Simulate extends App {
  def run
  Simulate.run {run}
}

object Simulate {
  var stop = false
  def run(body: => Unit){
    Thread.run{
      var sl:()=>Unit = null
      sl = ()=>{
        Thread.runOne
        Signal.sync
        SimTime.getRecents match {
          case Some(ss) =>
            ss foreach (_._notify)
            if (!stop){
              Thread{sl()}
            }
          case None => null
        }
      } : Unit
      body
      Component.run
      sl()
    }
  }
}