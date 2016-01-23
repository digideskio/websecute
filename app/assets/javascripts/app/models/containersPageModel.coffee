define ["knockout", "../../services/dockerClient", "../../services/websocket"], (ko, DockerClient, WebSocketFacade) ->

  class ContainersPageModel
    that = this

    constructor: () ->
      that = this
      @dockerClient = new DockerClient()

      @rawContainers = Array()
      @containers = ko.observableArray([])
      @selectedCont = 0
      @selectedContJson = ko.observable()

      @connect()

    connect: () ->
      @wsf = new WebSocketFacade("anonymous@gmail.com")

      @wsf.onOpenFunc = (event) ->
        that.dockerClient.containers(that.wsf)

      @wsf.onMessageFunc = (message, data) ->
        if (message).startsWith "Docker"
          that.loadContainers(JSON.parse(data))

    loadContainers: (data) ->
      for container in data
        that.rawContainers.push(JSON.stringify(container, null, ' '))
        that.containers.push({name: container.names[0], status: container.status})

    showContainer: (index) ->
      that.selectedCont = index
      that.selectedContJson(that.rawContainers[that.selectedCont])

    startSelectedContainer: ->
      # TODO

    execInSelectedContainer: ->
      console.log("execInSelectedContainer")

    stopSelectedContainer: ->
      console.log("stopSelectedContainer")

    deleteSelectedContainer: ->
      console.log("deleteSelectedContainer")

  return ContainersPageModel
