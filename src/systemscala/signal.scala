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

class Signal(val name: String, val parent: Component = Component.root, var oldVal: Any = null) {
  val fullname: String = (if (parent == null) ""  else parent.fullname + ".") + name
  var newVal = oldVal
  Signal.add(this)
  Event(fullname + "." + "read")
  Event(fullname + "." + "write")
  Event(fullname + "." + "changed")
  def event(n: String): Event = {
    Event.event(fullname + "." + n)
  }
  def onRead = event("read")
  def onWrite = event("write")
  def onChanged = event("changed")
  def read: Any = {
    onRead._notify
    newVal
  }
  def write(v: Any): Unit = {
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
  var insts = scala.collection.mutable.HashMap[String, Signal]()
  def apply(name: String, parent: Component = Component.root, v:Any=null) : Signal ={
    new Signal(name, parent, v)
  }
  def add[T](s: Signal){
    insts += (s.fullname -> s)
  }
  def signal(n: String): Signal = {
    insts.get(n) match {
      case None => throw new Exception("Cannot find signal " + n)
      case Some(s) => s
    }
  }
  def sync {
    for((n, s) <- insts){
      s.sync
    }
  }
}

