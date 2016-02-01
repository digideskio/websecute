#
# The DockerClient interface.
#
define () ->
  class DockerClient
    constructor: () ->

    info: (wsFacade) ->
      wsFacade.send({
        request: "DockerWSRequest"
        type: "info"
      })

    images: (wsFacade) ->
      wsFacade.send({
        request: "DockerWSRequest"
        type: "images"
      })

    containers: (wsFacade, filterKey, filterValue) ->
      if (typeof filterKey == 'undefined')
        filterKey = ""
        console.warn("filterKey undefined. Using default.")
      if (typeof filterValue == 'undefined')
        filterValue = ""
        console.warn("filterValue undefined. Using default.")
      wsFacade.send({
        request: "DockerWSRequest"
        type: "containers"
        filter: {
          filterKey: filterKey
          filterValue: filterValue
        }
      })

    start: (wsFacade, containerId) ->
      wsFacade.send({
        request: "DockerWSRequest"
        type: "start"
        id: "" + containerId
      })

    stop: (wsFacade, containerId) ->
      wsFacade.send({
        request: "DockerWSRequest"
        type: "stop"
        id: "" + containerId
      })

  return DockerClient