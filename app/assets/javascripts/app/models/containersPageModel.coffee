define ["knockout"], (ko) ->

  class ContainersPageModel
    constructor: () ->
      @containers = ko.observableArray([])

      @loadContainers()

    loadContainers: () ->
      that = this
      $.getJSON '/api/containers', (conts) ->
        for container in conts
          console.log(container)
          that.containers.unshift({name: container.id})

  return ContainersPageModel
