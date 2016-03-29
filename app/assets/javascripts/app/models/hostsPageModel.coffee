define ["knockout", "/assets/javascripts/services/dockerClient.js", "/assets/javascripts/services/websocket.js"], (ko, DockerClient, WebSocketFacade) ->

  class HostsPageModel
    that = this

    constructor: () ->
      that = this

      @sidebarItems = ko.observableArray([])

      @filterKey = ko.observable("")
      @filterValue = ko.observable("")

      @connect()

    connect: () ->
      @wsf = new WebSocketFacade("anonymous@gmail.com")

      @wsf.onOpenFunc = (event) ->
        that.requestHosts()

      @wsf.onMessageFunc = (type, event) ->
        t = event.type
        r = event.result
        if (type).startsWith "dockerResponse"
          that.handleWsMessage(event)
        else console.error("Unknown WS event type: " + t, r)

    handleWsMessage: (event) ->
      t = event.type
      r = event.result
      if t.startsWith("containersResponse")
        that.loadSidebarItems(JSON.parse(event.result))
      if t.startsWith("noWorkersErrorResponse")
        console.error("noWorkersErrorResponse")
      else console.error("Unhandled " + t, r)

    requestHosts: () ->
      @wsf.send({
        request: "DockerWSRequest"
        type: "containers"
        filter: {
          filterKey: ""
          filterValue: ""
        }
      })

    loadSidebarItems: (data) ->
      that.sidebarItems.removeAll()
      for container in data
        that.sidebarItems.push(container)

  return HostsPageModel
