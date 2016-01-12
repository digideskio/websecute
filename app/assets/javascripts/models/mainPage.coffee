#
# The main page.
#
# This class handles most of the user interactions with the buttons/menus/forms on the page, as well as manages
# the WebSocket connection.  It delegates to other classes to manage everything else.
#
define ["knockout"], (ko) ->

  class MainPageModel
    constructor: () ->
      @connecting = ko.observable("Not connected")
      @messages = ko.observableArray()
      @connect()

    # Connect function. Connects to the websocket, and sets up callbacks.
    connect: ->
      @ws = new WebSocket(jsRoutes.controllers.Application.stream("anonymous@gmail.com").webSocketURL())

      @ws.onopen = (event) =>
        @connecting("Connected")
        @ws.send(JSON.stringify({type: "RunMsg", scriptUrn: "urn"}))

      @ws.onclose = (event) =>
        # Need to handle reconnects in case of errors
        if (!event.wasClean)
          @connect()
          @connecting("Reconnecting...")

      @ws.onmessage = (event) =>
        @messages.push({ message: event.data })
        #json = JSON.parse(event.data)
        #if json.event == "user-positions"
        #  @ws.send(JSON.stringify(event))
        #else
        #  @ws.send(JSON.stringify(event))

    disconnect: ->
      @ws.close()

  return MainPageModel
