require [
  "knockout",
  "./assets/javascripts/app/models/containersPageModel",
  "bootstrap"
], (ko, ContainersPageModel) ->

  model = new ContainersPageModel
  ko.applyBindings(model)