define ["knockout", "../../services/dockerClient", "../../services/websocket"], (ko, DockerClient, WebSocketFacade) ->

  class ContainersPageModel
    that = this

    constructor: () ->
      that = this
      @dockerClient = new DockerClient()

      @containers = ko.observableArray([])
      @selectedCont = ko.observable({"command":"command","created":0,"id":"id","image":"image","names":["/name"],"ports":[],"labels":null,"status":"status"})

      @filterKey = ko.observable("")
      @filterValue = ko.observable("")

      @connect()

    connect: () ->
      @wsf = new WebSocketFacade("anonymous@gmail.com")

      @wsf.onOpenFunc = (event) ->
        that.getContainers()

      @wsf.onMessageFunc = (message, data) ->
        if (message).startsWith "DockerContainers"
          that.loadContainers(JSON.parse(data))
        if (message).startsWith "DockerStartContainer"
          console.log("DockerStartContainer. It does not indicate success or failure.")

    getContainers: () ->
      that.dockerClient.containers(that.wsf, that.filterKey(), that.filterValue())

    loadContainers: (data) ->
      that.containers.removeAll()
      for container in data
        that.containers.push(container)

    startSelectedContainer: ->
      that.dockerClient.start(that.wsf, that.selectedCont().id)

    execInSelectedContainer: ->
      console.log("execInSelectedContainer")

    stopSelectedContainer: ->
      that.dockerClient.stop(that.wsf, that.selectedCont().id)

    deleteSelectedContainer: ->
      console.log("deleteSelectedContainer")

  return ContainersPageModel
