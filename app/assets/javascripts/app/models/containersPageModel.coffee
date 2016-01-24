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
        if (message).startsWith "DockerContainers"
          that.loadContainers(JSON.parse(data))
        if (message).startsWith "DockerStartContainer"
          console.log("DockerStartContainer. It does not indicate success or failure.")

    loadContainers: (data) ->
      that.rawContainers = []
      that.containers.removeAll()
      for container in data
        that.rawContainers.push(container)
        that.containers.push({name: container.names[0], status: container.status})
      @showContainer(@selectedCont)

    showContainer: (index) ->
      that.selectedCont = index
      that.selectedContJson(JSON.stringify(that.rawContainers[that.selectedCont], null, ' '))

    startSelectedContainer: ->
      that.dockerClient.start(that.wsf, that.rawContainers[that.selectedCont].id)

    execInSelectedContainer: ->
      console.log("execInSelectedContainer")

    stopSelectedContainer: ->
      console.log("stopSelectedContainer")

    deleteSelectedContainer: ->
      console.log("deleteSelectedContainer")

  return ContainersPageModel
