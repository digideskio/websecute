#
# The WebSocket class. The goal is to create a default implementation. A particular page should only need
# to override the onMessage event handler.
# Inspired by:
# https://github.com/typesafehub/activator/blob/889970aab1f990cc477e4a9e1b3bab6b1897acef/ui/app/assets/commons/websocket.js
# https://github.com/typesafehub/ReactiveMaps/blob/47052cc04f522de799dad8b7af64e82acb304b04/app/assets/javascripts/models/mainPage.coffee
#
define () ->
  class WebSocketFacade
    that = this
    debug = true

    onOpenFunc: (event) ->
      debug && console.info("This is a placeholder onOpenFunc method.")

    onMessageFunc: (message, data) ->
      debug && console.info("This is a placeholder onMessageFunc method.")

    constructor: (email) -> # TODO: use session instead of email
      that = this
      @email = email
      @connect()

    connect: () ->
      @ws = new WebSocket(jsRoutes.controllers.Application.stream(@email).webSocketURL())
      @ws.onopen = @onOpen
      @ws.onmessage = @onMessage
      @ws.onerror = @onError
      @ws.onclose = @onClose

    onOpen: (event) ->
      debug && console.info("WS opening", event)
      that.onOpenFunc(event)

    onMessage: (event) ->
      debug && console.info("WS message: ", event)
      json = JSON.parse(event.data)
      that.onMessageFunc(json.type, json.event)

    onError: (event) ->
      debug && console.error("WS error", event)

    onClose: (event) ->
      debug && console.info("WS closing: ", event)

    withWebSocket: (wsFunc) ->
      if (@ws && @ws.readyState == @ws.OPEN)
        wsFunc(@ws)
      else debug && console.error("Failed to send message. WS is not open.")

    send: (msg) ->
      @withWebSocket((@ws) ->
        sMsg = JSON.stringify(msg)
        debug && console.info("Sending: ", msg)
        @ws.send(sMsg)
      )

  return WebSocketFacade