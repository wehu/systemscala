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

class Signal[T: Manifest](val name: String, var oldVal: T, val parent: Component = Component.root) {
  val fullname: String = (if (parent == null) ""  else parent.fullname + ".") + name
  var newVal = oldVal
  Signal.add(fullname, this)
  override def toString() = "Signal[" + manifest[T] + "] " + fullname
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
  var insts = scala.collection.mutable.HashMap[String, Any]()
  def apply[T: Manifest](name: String, v:T, parent: Component = Component.root): Signal[T] ={
    new Signal[T](name, v, parent)
  }
  def add(fullname: String, s: Any){
    insts.get(fullname) match {
      case None => insts += (fullname -> s)
      case _ => throw new Exception("Attempt to redefine a signal " + fullname)
    }
  }
  def signal[T](n: String): Signal[T] = {
    insts.get(n) match {
      case Some(s: Signal[T]) => s
      case _ => throw new Exception("Cannot find signal " + n)
    }
  }
  def sync {
    for((n, s) <- insts){
      s match {
        case s: Signal[_] => s.sync
        case _ =>
      }
    }
  }
}

