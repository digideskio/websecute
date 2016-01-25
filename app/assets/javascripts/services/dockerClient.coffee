#
# The DockerClient interface.
#
define () ->
  class DockerClient
    constructor: () ->

    info: (wsFacade) ->
      wsFacade.send({
        message: "DockerInfo"
        data: ""
      })

    images: (wsFacade) ->
      wsFacade.send({
        message: "DockerImages"
        data: ""
      })

    containers: (wsFacade, filterKey, filterValue) ->
      if (typeof filterKey == 'undefined')
        filterKey = ""
        console.warn("filterKey undefined. Using default.")
      if (typeof filterValue == 'undefined')
        filterValue = ""
        console.warn("filterValue undefined. Using default.")
      wsFacade.send({
        message: "DockerContainers"
        data: JSON.stringify({
          filterKey: filterKey
          filterValue: filterValue
        })
      })

    start: (wsFacade, containerId) ->
      wsFacade.send({
        message: "DockerStartContainer"
        data: "" + containerId
      })

    stop: (wsFacade, containerId) ->
      wsFacade.send({
        message: "DockerStopContainer"
        data: "" + containerId
      })

  return DockerClient