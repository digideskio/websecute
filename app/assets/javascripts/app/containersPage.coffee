require [
  "knockout",
  "/assets/javascripts/app/models/containersPageModel.js",
  "bootstrap"
], (ko, ContainersPageModel) ->

  model = new ContainersPageModel
  ko.applyBindings(model)