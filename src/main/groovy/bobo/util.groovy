package bobo

import org.yaml.snakeyaml.Yaml as SnakeYaml

static def yaml(String resource) {
  final InputStream inputSt = util.getResourceAsStream(resource)
  final SnakeYaml snakeYaml = new SnakeYaml()


  return snakeYaml.load(inputSt)
}
