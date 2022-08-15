#!/usr/bin/env bash

LEGENDS=( "{{pod}}:{{\`{{pod}}\`}}"
        "{{pod_name}}:{{\`{{pod_name}}\`}}"
        "{{nodename}}:{{\`{{nodename}}\`}}"
        "{{node}}:{{\`{{node}}\`}}"
        "{{destination_node}}:{{\`{{destination_node}}\`}}"
        "{{reason}}:{{\`{{reason}}\`}}"
        "{{instance}}:{{\`{{instance}}\`}}"
        "{{condition}}:{{\`{{condition}}\`}}"
        "{{namespace}}:{{\`{{namespace}}\`}}"
        "{{device}}:{{\`{{device}}\`}}"
        "{{deployment}}:{{\`{{deployment}}\`}}"
        "{{container}}:{{\`{{container}}\`}}"
        "{{exported_instance}}:{{\`{{exported_instance}}\`}}"
        "{{input}}:{{\`{{input}}\`}}"
        "{{output}}:{{\`{{output}}\`}}"
        "{{db}}:{{\`{{db}}\`}}"
        "{{cmd}}:{{\`{{cmd}}\`}}"
        "{{role}}:{{\`{{role}}\`}}"
        "{{job}}:{{\`{{job}}\`}}"
        "{{path}}:{{\`{{path}}\`}}"
        "{{quantile}}:{{\`{{quantile}}\`}}"
        "{{level}}:{{\`{{level}}\`}}"
        "{{addr}}:{{\`{{addr}}\`}}"
        "{{type}}:{{\`{{type}}\`}}"
        "{{vm}}:{{\`{{vm}}\`}}"
        "{{status}}:{{\`{{status}}\`}}"
        "{{code}}:{{\`{{code}}\`}}"
        "{{direction}}:{{\`{{direction}}\`}}"
        "{{filter_name}}:{{\`{{filter_name}}\`}}"
        "{{backend}}:{{\`{{backend}}\`}}"
        "{{persistentvolumeclaim}}:{{\`{{persistentvolumeclaim}}\`}}"
        "{{access_mode}}:{{\`{{access_mode}}\`}}"
        "{{resource}}:{{\`{{resource}}\`}}"
        "{{verb}}:{{\`{{verb}}\`}}"
        "{{mountpoint}}:{{\`{{mountpoint}}\`}}"
        "{{state}}:{{\`{{state}}\`}}"
        "{{cpu}}:{{\`{{cpu}}\`}}"
        "{{info}}:{{\`{{info}}\`}}"
        "{{chip}}:{{\`{{chip}}\`}}"
        "{{sensor}}:{{\`{{sensor}}\`}}"
        "{{name}}:{{\`{{name}}\`}}"
        "{{power_supply}}:{{\`{{power_supply}}\`}}"
        "{{collector}}:{{\`{{collector}}\`}}"
        "{{interface}}:{{\`{{interface}}\`}}"
        "{{queue}}:{{\`{{queue}}\`}}"
        "{{vhost}}:{{\`{{vhost}}\`}}"
        "{{statename}}:{{\`{{statename}}\`}}" )

for legend in "${LEGENDS[@]}" ; do
    KEY="${legend%%:*}"
    VALUE="${legend##*:}"

    for f in $(find dashboards/ -name '*.json'); do
      sed -i "s/$KEY/$VALUE/g" $f
    done
done