package bobo

import static spark.Spark.get
import static spark.Spark.path
import static groovy.json.JsonOutput.toJson

import bobo.jdbc as J
import bobo.util as U
import bobo.rabbit as R
import bobo.common as C

static void main(args) {
  // Init watchers
  initWatchers()

  // Expose watchers
  path("/api/v1") {
    get("/services/:name") { req, res ->
      String service = req.params(":name")
      res.type('application/json')
      toJson(service: service,
             status: C.STORE.get(service))
    }
  }
}

static void initWatchers() {
  def services = U.yaml("/test.yml")

  services.each { serviceMap ->
    def type = (serviceMap.keySet() - "name").first()
    def clazz = ClassLoader.systemClassLoader.loadClass("bobo.$type")

    println "[MODULE]: ${clazz.name}"
    clazz.watch(serviceMap.name, serviceMap."$type")
  }
}
