#
# The DockerClient interface.
#
define () ->
  class DockerClient
    # @ws The WebSocket to send updates to
    constructor: (ws) ->
      @ws = ws

    handleResult: (message, json) ->
      if message == "DockerInfo"
        return json
      if message == "DockerImages"
        return json
      if message == "DockerContainers"
        return json

    # Send the DockerInfo command
    info: ->
      @ws.send(JSON.stringify
          message: "DockerInfo"
          data: ""
      )

    images: ->
      @ws.send(JSON.stringify
          message: "DockerImages"
          data: ""
      )

    containers: ->
      @ws.send(JSON.stringify
          message: "DockerContainers"
          data: ""
      )

  return DockerClient