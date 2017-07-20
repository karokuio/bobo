package bobo

import static spark.Spark.get
import static spark.Spark.path
import static groovy.json.JsonOutput.toJson

import bobo.jdbc as J
import bobo.util as U
import bobo.rabbit as R

static void main(args) {
  def keyStore = [:]
  def services = U.yaml("/test.yml")

  services.each { serviceMap ->
    def type = (serviceMap.keySet() - "name").first()
    def clazz = ClassLoader.systemClassLoader.loadClass("bobo.$type")

    println "[MODULE]: ${clazz.name}"

    clazz.watch(serviceMap."$type")
  }

  path("/api/v1") {
    get("/status") { req, res ->
      res.type('application/json')
      toJson(service: "bobo", status: "ok")
    }

    get("/services/:name"){ req, res ->
      res.type('application/json')
      toJson(service: req.params(':name'), status: "ok")
    }
  }
}
