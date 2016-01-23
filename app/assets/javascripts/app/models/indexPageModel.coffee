#
# The index page.
#
# This class handles most of the user interactions with the buttons/menus/forms on the page, as well as manages
# the WebSocket connection.  It delegates to other classes to manage everything else.
#
define ["knockout", "../../services/dockerClient", "../../services/websocket"], (ko, DockerClient, WebSocketFacade) ->

  class IndexPageModel
    constructor: () ->
      @dockerClient = new DockerClient()
      @connecting = ko.observable("Not connected")
      @messages = ko.observableArray()
      @connect()

    # Connect function. Connects to the websocket, and sets up callbacks.
    connect: ->
      that = this

      @wsf = new WebSocketFacade("anonymous@gmail.com")
      @wsf.onMessageFunc = (message, data) ->
        if (message).startsWith "Docker"
          that.messages.unshift({ message: JSON.stringify(JSON.parse(data), null, ' ') })

    dockerInfo: ->
      @dockerClient.info(@wsf)
    dockerImages: ->
      @dockerClient.images(@wsf)
    dockerContainers: ->
      @dockerClient.containers(@wsf)

  return IndexPageModel
