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
        @messages.push({ message: event.data })
        #json = JSON.parse(event.data)
        #if json.event == "user-positions"
        #  @ws.send(JSON.stringify(event))
        #else
        #  @ws.send(JSON.stringify(event))

    disconnect: ->
      @ws.close()

    dockerInfo: ->
      @dockerClient().info()
    dockerImages: ->
      @dockerClient().images()

  return MainPageModel
