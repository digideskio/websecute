#
# The DockerClient interface.
#
define () ->
  class DockerClient
    constructor: () ->

    # Send the DockerInfo command
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

  return DockerClient