package systemscala

object Logger {
  def info(args: Any*){
    println("[INFO #" + SimTime.timeline + "]: " + args.mkString)
  }
  def debug(args: Any*){
    println("[DEBUG #" + SimTime.timeline + "]: " + args.mkString)
  }
  def error(args: Any*){
    println("[ERROR #" + SimTime.timeline + "]: " + args.mkString)
  }
  def warn(args: Any*){
    println("[WARN #" + SimTime.timeline + "]: " + args.mkString)
  }
}