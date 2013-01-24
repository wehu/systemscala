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

class Component(val name: String, val parent: Component = Component.root) {
  import scala.util.continuations._
  val fullname: String = (if (parent == null) ""  else parent.fullname + ".") + name
  Component.add(this)
  def build = {}
  def connect = {}
  def run : Unit@cps[Unit] = {}
  var threads = scala.collection.mutable.Queue[()=>Unit@cps[Unit]]()
  def wait(e: Event) : Unit@cps[Unit] = {
    e._wait()
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
  def repeat(body: =>Unit@cps[Unit]){
    always(SimTime(0)) {body}
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