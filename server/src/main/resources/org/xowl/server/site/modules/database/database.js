'use strict';

angular.module('xOWLServer.database', ['ngRoute'])

	.config(['$routeProvider', function ($routeProvider) {
		$routeProvider.when('/database/:id', {
			templateUrl: 'modules/database/database.html',
			controller: 'DatabaseCtrl'
		});
	}])

	.controller('DatabaseCtrl', ['$rootScope', '$scope', '$sce', '$routeParams', '$location', function ($rootScope, $scope, $sce, $routeParams, $location) {
		$scope.database = {
			name: $routeParams.id,
			status: true
		}
		$scope.history = [];
		$scope.mimeTypes = XOWL_MIME_TYPES;

		$rootScope.xowl.getEntailmentFor(function (code, type, content) {
			if (code === 200) {
				$scope.database.entailment = content;
				setupEntailment($scope.database.entailment);
			} else {
				$scope.messages = $sce.trustAsHtml(getErrorFor(code, content));
			}
		}, $scope.database.name);

		reloadRules($rootScope, $scope, $sce);
		reloadPrivileges($rootScope, $scope, $sce);
		document.getElementById("rule-def-new").value = DEFAULT_RULE;
		document.getElementById("sparql").value = DEFAULT_QUERY;
		document.getElementById('import-file').addEventListener('change', onFileSelected, false);

		$scope.onSetEntailment = function () {
			var regime = getEntailment();
			$rootScope.xowl.setEntailmentFor(function (code, type, content) {
				if (code === 200) {
					$scope.messages = $sce.trustAsHtml(getSuccess("The entailment regime was set."));
					$scope.database.entailment = regime;
				} else {
					setupEntailment($scope.database.entailment);
					$scope.messages = $sce.trustAsHtml(getErrorFor(code, content));
				}
			}, $scope.database.name, regime);
		}

		$scope.onDBDrop = function () {
			$rootScope.xowl.dropDatabase(function (code, type, content) {
				if (code === 200) {
					$location.path("/databases");
				} else {
					$scope.messages = $sce.trustAsHtml(getErrorFor(code, content));
				}
			}, $scope.database.name);
		}

		$scope.onRevoke = function (access, privilege) {
			var user = access.user;
			$rootScope.xowl.revokeDB(function (code, type, content) {
				if (code === 200) {
					$scope.messages = $sce.trustAsHtml(getSuccess("Success!"));
					reloadPrivileges($rootScope, $scope, $sce);
				} else {
					$scope.messages = $sce.trustAsHtml(getErrorFor(code, content));
				}
			}, $scope.database.name, privilege, user);
			if (user === $rootScope.xowl.userName) {
				reloadUserData($rootScope);
			}
		}

		$scope.onGrant = function () {
			var user = document.getElementById('field-grant-user').value;
			var privilege = document.getElementById('field-grant-privilege').value;
			$rootScope.xowl.grantDB(function (code, type, content) {
				if (code === 200) {
					$scope.messages = $sce.trustAsHtml(getSuccess("Success!"));
					reloadPrivileges($rootScope, $scope, $sce);
				} else {
					$scope.messages = $sce.trustAsHtml(getErrorFor(code, content));
				}
			}, $scope.database.name, privilege, user);
			if (user === $rootScope.xowl.userName) {
				reloadUserData($rootScope);
			}
		}

		$scope.onNewRule = function () {
			var data = document.getElementById("rule-def-new").value;
			$rootScope.xowl.addDBRule(function (code, type, content) {
				if (code === 200) {
					$scope.messages = $sce.trustAsHtml(getSuccess("Added new rule."));
					reloadRules($rootScope, $scope, $sce);
				} else {
					$scope.messages = $sce.trustAsHtml(getErrorFor(code, content));
				}
			}, $scope.database.name, data);
		}

		$scope.onRemoveRule = function (name) {
			$rootScope.xowl.removeDBRule(function (code, type, content) {
				if (code === 200) {
					$scope.messages = $sce.trustAsHtml(getSuccess("Removed rule " + name));
					reloadRules($rootScope, $scope, $sce);
				} else {
					$scope.messages = $sce.trustAsHtml(getErrorFor(code, content));
				}
			}, $scope.database.name, name);
		}

		$scope.onActivateRule = function (name) {
			$rootScope.xowl.activateDBRule(function (code, type, content) {
				if (code === 200) {
					$scope.messages = $sce.trustAsHtml(getSuccess("Activated rule " + name));
					reloadRules($rootScope, $scope, $sce);
				} else {
					$scope.messages = $sce.trustAsHtml(getErrorFor(code, content));
				}
			}, $scope.database.name, name);
		}

		$scope.onDeactivateRule = function (name) {
			$rootScope.xowl.deactivateDBRule(function (code, type, content) {
				if (code === 200) {
					$scope.messages = $sce.trustAsHtml(getSuccess("Deactivated rule " + name));
					reloadRules($rootScope, $scope, $sce);
				} else {
					$scope.messages = $sce.trustAsHtml(getErrorFor(code, content));
				}
			}, $scope.database.name, name);
		}

		$scope.onViewRule = function (name) {
			$rootScope.xowl.getDBRuleDefinition(function (code, type, content) {
				if (code === 200) {
					document.getElementById("rule-modal-title").innerHTML = $sce.trustAsHtml(name);
					document.getElementById("rule-modal-definition").value = content;
					$('#rule-modal').modal('show');
				} else {
					$scope.messages = $sce.trustAsHtml(getErrorFor(code, content));
				}
			}, $scope.database.name, name);
		}

		$scope.onSPARQL = function () {
			var query = document.getElementById("sparql").value;
			var date = new Date();
			$scope.history.push({
				name: "[" + ($scope.history.length + 1).toString() + "] @ " + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds(),
				query: query
			});
			$rootScope.xowl.sparql(function (code, type, content) {
				if (code === 200) {
					onSPARQLResults($scope, type, content);
				} else {
					$scope.messages = $sce.trustAsHtml(getErrorFor(code, content));
				}
			}, $scope.database.name, query);
		}

		$scope.onHistory = function (part) {
			document.getElementById("sparql").value = part.query;
		}

		$scope.onImport = function () {
			var file = document.getElementById("import-file").files[0];
			var selectedMIME = document.getElementById("import-file-type").value;
			var progressBar = document.getElementById("import-progress");
			progressBar['aria-valuenow'] = 0;
			progressBar.style.width = "0%";
			progressBar.classList.remove("progress-bar-success");
			progressBar.classList.remove("progress-bar-error");
			progressBar.innerHTML = null;
			var reader = new FileReader();
			reader.onprogress = function (event) {
				var ratio = 50 * event.loaded / event.total;
				progressBar['aria-valuenow'] = ratio;
				progressBar.style.width = ratio.toString() + "%";
				progressBar.innerHTML = $sce.trustAsHtml("Reading ...");
			}
			reader.onloadend = function (event) {
				if (reader.error !== null) {
					$scope.messages = $sce.trustAsHtml(getError(reader.error.toString()));
					progressBar['aria-valuenow'] = 100;
					progressBar.style.width = "100%";
					progressBar.classList.add("progress-bar-error");
					progressBar.innerHTML = $sce.trustAsHtml("Error");
					return;
				}
				progressBar.innerHTML = $sce.trustAsHtml("Sending ...");
				$rootScope.xowl.upload(function (code, type, content) {
					if (code === 200) {
						$scope.messages = $sce.trustAsHtml(getSuccess("Success!"));
						progressBar.classList.add("progress-bar-success");
						progressBar.innerHTML = $sce.trustAsHtml("Success!");
					} else {
						$scope.messages = $sce.trustAsHtml(getErrorFor(code, content));
						progressBar.classList.add("progress-bar-error");
						progressBar.innerHTML = $sce.trustAsHtml("Error");
					}
					progressBar['aria-valuenow'] = 100;
					progressBar.style.width = "100%";
				}, $scope.database.name, selectedMIME, reader.result);
			}
			reader.readAsBinaryString(file);
		}
	}]);

var DEFAULT_RULE =
	"@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>.\n" +
	"@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>.\n" +
	"@prefix xsd: <http://www.w3.org/2001/XMLSchema#>.\n" +
	"@prefix owl: <http://www.w3.org/2002/07/owl#>.\n" +
	"@prefix xowl: <http://xowl.org/store/rules/xowl#>.\n\n" +
	"rule xowl:myrule distinct {\n" +
	"    ?x rdf:type ?y\n" +
	"    NOT (?x rdf:type owl:Class)\n" +
	"} => {\n" +
	"    ?x rdf:type xowl:MyClass\n" +
	"}";

var DEFAULT_QUERY =
	"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
	"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
	"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
	"PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
	"PREFIX xowl: <http://xowl.org/store/rules/xowl#>\n\n" +
	"SELECT DISTINCT ?x ?y WHERE { GRAPH ?g { ?x a ?y } }";

function onFileSelected(evt) {
    var file = evt.target.files[0];
	var mime = file.type;
	var fileType = null;
	for (var i = 0; i != XOWL_MIME_TYPES.length; i++) {
		if (XOWL_MIME_TYPES[i].value === mime) {
			fileType = XOWL_MIME_TYPES[i];
			break;
		}
		for (var j = 0; j != XOWL_MIME_TYPES[i].extensions.length; j++) {
			var suffix = XOWL_MIME_TYPES[i].extensions[j];
			if (file.name.indexOf(suffix, file.name.length - suffix.length) !== -1) {
				fileType = XOWL_MIME_TYPES[i];
				break;
			}
		}
	}
	if (fileType !== null) {
		document.getElementById("import-file-type").value = fileType.value;
	}
}

function reloadPrivileges($rootScope, $scope, $sce) {
	$rootScope.xowl.getDatabasePrivileges(function (code, type, content) {
        if (code === 200) {
			$scope.privileges = content;
        } else {
			$scope.messages = $sce.trustAsHtml(getErrorFor(code, content));
        }
	}, $scope.database.name);
}

function reloadRules($rootScope, $scope, $sce) {
	$rootScope.xowl.getDBRules(function (code, type, content) {
		if (code !== 200) {
			$scope.messages = $sce.trustAsHtml(getErrorFor(code, content));
		} else {
			$scope.rules = [];
			for (var i = 0; i != content.length; i++) {
				$scope.rules.push({ name: content[i], isActive: false });
			}
			$rootScope.xowl.getDBActiveRules(function (code, type, content) {
				for (var i = 0; i != content.length; i++) {
					for (var j = 0; j != $scope.rules.length; j++) {
						if ($scope.rules[j].name === content[i]) {
							$scope.rules[j].isActive = true;
						}
					}
				}
			}, $scope.database.name);
		}
	}, $scope.database.name);
}

function onSPARQLResults($scope, type, content) {
	$scope.data = { headers: [''], rows: [] };
	var index = type.indexOf(";");
    if (index !== -1)
        type = type.substring(0, index);
	if (type === "application/sparql-results+json") {
		var data = JSON.parse(content);
		if (data.hasOwnProperty("boolean")) {
			var isSuccess = data.boolean;
			if (isSuccess)
				alert("OK");
			else
				alert(data.error);
			return;
		}
		var vars = data.head.vars;
		var solutions = data.results.bindings;
		for (var i = 0; i != vars.length; i++) {
			$scope.data.headers.push(vars[i]);
		}
		for (var i = 0; i != solutions.length; i++) {
			var solution = solutions[i];
			var row = { cells: [(i + 1).toString()] };
			for (var j = 0; j != vars.length; j++) {
				if (solution.hasOwnProperty(vars[j])) {
					row.cells.push(rdfToString(solution[vars[j]]));
				} else {
					row.cells.push('');
				}
			}
			$scope.data.rows.push(row);
		}
	} else if (type === "application/n-quads") {
		$scope.data.headers = ['s', 'p', 'o', 'g'];
		var entities = parseNQuads(content);
		var names = Object.getOwnPropertyNames(entities);
		for (var p = 0; p != names.length; p++) {
			var entity = entities[names[p]];
			for (j = 0; j != entity.properties.length; j++) {
				var property = entity.properties[j];
				var row = { cells: [] };
				if (entity.id.startsWith("http://"))
					row.cells.push(entity.id);
				else
					row.cells.push('_:' + entity.id);
				row.cells.push(property.id);
				row.cells.push(rdfToString(property.value));
				row.cells.push(property.graph);
				$scope.data.rows.push(row);
			}
		}
	}
}

function rdfToString(value) {
    if (value.type === "uri" || value.type === "iri") {
        return value.value;
    } else if (value.type === "bnode") {
        return '_:' + value.value;
    } else if (value.type === "blank") {
		return '_:' + value.id;
    } else if (value.type === "variable") {
		return '?' + value.value;
    } else if (value.hasOwnProperty("lexical")) {
		return '"' + value.lexical + '"' +
			(value.datatype !== null ? '^^<' + value.datatype + '>' : '') +
			(value.lang !== null ? '@' + value.lang : '');
    } else {
		return '"' + value.value + '"' +
			(value.datatype !== null ? '^^<' + value.datatype + '>' : '') +
			(value.hasOwnProperty("xml:lang") ? '@' + value["xml:lang"] : '');
    }
}

function getEntailment() {
	if (document.getElementById('entailment-option-none').checked)
		return 'none';
	if (document.getElementById('entailment-option-simple').checked)
		return 'simple';
	if (document.getElementById('entailment-option-rdf').checked)
		return 'RDF';
	if (document.getElementById('entailment-option-rdfs').checked)
		return 'RDFS';
	if (document.getElementById('entailment-option-owl2-rdf').checked)
		return 'OWL2_RDF';
	if (document.getElementById('entailment-option-owl2-direct').checked)
		return 'OWL2_DIRECT';
	return null;
}

function setupEntailment(regime) {
	switch (regime) {
		case 'none':
			document.getElementById('entailment-option-none').checked = true;
			document.getElementById('entailment-option-simple').checked = false;
			document.getElementById('entailment-option-rdf').checked = false;
			document.getElementById('entailment-option-rdfs').checked = false;
			document.getElementById('entailment-option-owl2-rdf').checked = false;
			document.getElementById('entailment-option-owl2-direct').checked = false;
			break;
		case 'simple':
			document.getElementById('entailment-option-none').checked = false;
			document.getElementById('entailment-option-simple').checked = true;
			document.getElementById('entailment-option-rdf').checked = false;
			document.getElementById('entailment-option-rdfs').checked = false;
			document.getElementById('entailment-option-owl2-rdf').checked = false;
			document.getElementById('entailment-option-owl2-direct').checked = false;
			break;
		case 'RDF':
			document.getElementById('entailment-option-none').checked = false;
			document.getElementById('entailment-option-simple').checked = false;
			document.getElementById('entailment-option-rdf').checked = true;
			document.getElementById('entailment-option-rdfs').checked = false;
			document.getElementById('entailment-option-owl2-rdf').checked = false;
			document.getElementById('entailment-option-owl2-direct').checked = false;
			break;
		case 'RDFS':
			document.getElementById('entailment-option-none').checked = false;
			document.getElementById('entailment-option-simple').checked = false;
			document.getElementById('entailment-option-rdf').checked = false;
			document.getElementById('entailment-option-rdfs').checked = true;
			document.getElementById('entailment-option-owl2-rdf').checked = false;
			document.getElementById('entailment-option-owl2-direct').checked = false;
			break;
		case 'OWL2_RDF':
			document.getElementById('entailment-option-none').checked = false;
			document.getElementById('entailment-option-simple').checked = false;
			document.getElementById('entailment-option-rdf').checked = false;
			document.getElementById('entailment-option-rdfs').checked = false;
			document.getElementById('entailment-option-owl2-rdf').checked = true;
			document.getElementById('entailment-option-owl2-direct').checked = false;
			break;
		case 'OWL2_DIRECT':
			document.getElementById('entailment-option-none').checked = false;
			document.getElementById('entailment-option-simple').checked = false;
			document.getElementById('entailment-option-rdf').checked = false;
			document.getElementById('entailment-option-rdfs').checked = false;
			document.getElementById('entailment-option-owl2-rdf').checked = false;
			document.getElementById('entailment-option-owl2-direct').checked = true;
			break;
		default:
			break;
	}
}