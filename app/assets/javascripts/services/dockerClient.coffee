#
# The DockerClient interface.
#
define () ->
  class DockerClient
    constructor: () ->

    info: (wsFacade) ->
      wsFacade.send({
        message: "DockerInfo"
        data: ""
      })

    images: (wsFacade) ->
      wsFacade.send({
        message: "DockerImages"
        data: ""
      })

    containers: (wsFacade) ->
      wsFacade.send({
        message: "DockerContainers"
        data: ""
      })

    start: (wsFacade, containerId) ->
      wsFacade.send({
        message: "DockerStartContainer"
        data: "" + containerId
      })

  return DockerClient