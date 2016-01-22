define ["knockout", "../../services/dockerClient"], (ko, DockerClient) ->

  class ContainersPageModel
    that = this

    constructor: () ->
      @rawContainers = Array()
      @containers = ko.observableArray([])
      @selectedCont = 0
      @selectedContJson = ko.observable()

      @getContainers()

    getContainers: () ->
      that = this # TODO: Why is this necessary?
      $.getJSON jsRoutes.controllers.Application.getContainers().url, (data) ->
        that.loadContainers(data)

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
