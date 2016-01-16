#
# The main page.
#
# This class handles most of the user interactions with the buttons/menus/forms on the page, as well as manages
# the WebSocket connection.  It delegates to other classes to manage everything else.
#
define ["knockout", "dockerClient"], (ko, DockerClient) ->

  class MainPageModel
    constructor: () ->
      @dockerClient = ko.observable()
      @connecting = ko.observable("Not connected")
      @messages = ko.observableArray()
      @connect()

    # Connect function. Connects to the websocket, and sets up callbacks.
    connect: ->
      @ws = new WebSocket(jsRoutes.controllers.Application.stream("anonymous@gmail.com").webSocketURL())

      @ws.onopen = (event) =>
        @dockerClient(new DockerClient(@ws))
        @connecting("Connected")

      @ws.onmessage = (event) =>
        data = JSON.parse(JSON.parse(event.data).dummy)
        @messages.unshift({ message: JSON.stringify(data, null, ' ') })

    disconnect: ->
      @ws.close()

    dockerInfo: ->
      @dockerClient().info()
    dockerImages: ->
      @dockerClient().images()
    dockerContainers: ->
      @dockerClient().containers()

  return MainPageModel
