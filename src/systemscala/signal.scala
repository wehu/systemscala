/*
* Copyright 2013 The SystemScala Authors. All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*/

package systemscala

object SignalMgr{
  abstract class SignalMgr[T: Manifest] {
    var insts: scala.collection.mutable.HashMap[String, Signal[T]]
    def typeString = manifest[T].toString()
  }
  implicit object IntSignal extends SignalMgr[Int]{ 
    var insts = scala.collection.mutable.HashMap[String, Signal[Int]]()
  }
  implicit object StringSignal extends SignalMgr[String]{ 
    var insts = scala.collection.mutable.HashMap[String, Signal[String]]()
  }
  implicit object FloatSignal extends SignalMgr[Float]{ 
    var insts = scala.collection.mutable.HashMap[String, Signal[Float]]()
  }
  //TODO More types
  //default
  implicit object AnySignal extends SignalMgr[Any]{ 
    var insts = scala.collection.mutable.HashMap[String, Signal[Any]]()
  }
}
  
import SignalMgr._

class Signal[T](val name: String, var oldVal: T, val parent: Component = Component.root)
  (implicit sm: SignalMgr[T]) {
  val fullname: String = (if (parent == null) ""  else parent.fullname + ".") + name
  var newVal = oldVal
  Signal.add[T](this)(sm)
  override def toString() = "Signal[" + sm.typeString + "] " + fullname
  Event(fullname + "." + "read")
  Event(fullname + "." + "write")
  Event(fullname + "." + "changed")
  def event(n: String): Event = {
    Event.event(fullname + "." + n)
  }
  def onRead = event("read")
  def onWrite = event("write")
  def onChanged = event("changed")
  def read: T = {
    onRead._notify
    newVal
  }
  def write(v: T): Unit = {
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
  def apply[T](name: String, v:T, parent: Component = Component.root)(implicit sm: SignalMgr[T]) : Signal[T] ={
    new Signal[T](name, v, parent)(sm)
  }
  def add[T](s: Signal[T])(implicit sm: SignalMgr[T]){
    sm.insts += (s.fullname -> s)
  }
  def signal[T](n: String)(implicit sm: SignalMgr[T]): Signal[T] = {
    sm.insts.get(n) match {
      case None => throw new Exception("Cannot find signal " + n)
      case Some(s) => s
    }
  }
  def sync[T](implicit sm: SignalMgr[T]) {
    for((n, s) <- sm.insts){
      s.sync
    }
  }
}

