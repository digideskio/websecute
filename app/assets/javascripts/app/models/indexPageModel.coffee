#
# The index page.
#
# This class handles most of the user interactions with the buttons/menus/forms on the page, as well as manages
# the WebSocket connection.  It delegates to other classes to manage everything else.
#
define ["knockout", "../../services/dockerClient", "../../services/websocket"], (ko, DockerClient, WebSocketFacade) ->

  class IndexPageModel
    that = this
    constructor: () ->
      that = this
      @dockerClient = new DockerClient()
      @connecting = ko.observable("Not connected")
      @messages = ko.observableArray()
      @connect()

    # Connect function. Connects to the websocket, and sets up callbacks.
    connect: ->
      @wsf = new WebSocketFacade("anonymous@gmail.com")
      @wsf.onMessageFunc = (type, event) ->
        if (type).startsWith "dockerResponse"
          that.handleDockerResponse(event)

    handleDockerResponse: (event) ->
      t = event.type
      r = event.result
      if t.startsWith("infoResponse")
        that.messages.unshift({ message: JSON.stringify(JSON.parse(r), null, ' ') })
      else if t.startsWith("imagesResponse")
        that.messages.unshift({ message: JSON.stringify(JSON.parse(r), null, ' ') })
      else if t.startsWith("containersResponse")
        that.messages.unshift({ message: JSON.stringify(JSON.parse(r), null, ' ') })

    dockerInfo: ->
      @dockerClient.info(@wsf)
    dockerImages: ->
      @dockerClient.images(@wsf)
    dockerContainers: ->
      @dockerClient.containers(@wsf)

  return IndexPageModel
