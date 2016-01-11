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
      @receivedCnt = ko.observable(0)
      @connect()

    # Connect function. Connects to the websocket, and sets up callbacks.
    connect: ->
      @ws = new WebSocket(jsRoutes.controllers.Application.stream("anonymous@gmail.com").webSocketURL())

      # When the websocket opens, ...
      @ws.onopen = (event) =>
        @connecting("Connected")
        @ws.send(JSON.stringify({type: "RunMsg", scriptUrn: "urn"}))
        console.log(JSON.stringify({type: "RunMsg", scriptUrn: "urn"}))

      @ws.onclose = (event) =>
        # Need to handle reconnects in case of errors
        if (!event.wasClean)
          @connect()
          @connecting("Reconnecting...")

      # Handle the stream of feature updates
      @ws.onmessage = (event) =>
        console.log("msg")
        @receivedCnt(@receivedCnt() + 1)
        json = JSON.parse(event.data)
        if json.event == "user-positions"
          @ws.send(JSON.stringify(event))
        else
          @ws.send(JSON.stringify(event))

    # Disconnect the web socket
    disconnect: ->
      @ws.close()

  return MainPageModel
