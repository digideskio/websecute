#
# The DockerClient interface.
#
define () ->
  class DockerClient
    # @ws The WebSocket to send updates to
    constructor: (ws) ->
      @ws = ws

    # Send the DockerInfo command
    info: ->
      @ws.send(JSON.stringify
          event: "docker-info-cmd"
          dummy: ""
      )

    images: ->
      @ws.send(JSON.stringify
          event: "docker-images-cmd"
          dummy: ""
      )

  return DockerClient