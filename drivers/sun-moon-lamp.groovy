/*
 * Sun Moon Lamp Driver
 * 
 * 1.0.0 - Initial release
 * 
 */
metadata {
    definition(name: "Sun Moon Lamp", namespace: "wastedabuser", author: "wastedabsuer", importUrl: "https://raw.githubusercontent.com/wastedabuser/hubitat/master/drivers/sun-moon-lamp.groovy") {
        capability "Actuator"
        capability "Switch"
        capability "Sensor"
        
        command "preset", [[name:"Preset", type:"ENUM", description:"Activate preset", constraints: ["off","sun","sunset","fullmoon","gibous","quarter","crescent"]]]
    }
}

preferences {
    section("URIs") {
        input "ip", "text", title: "IP Address", required: false
        input name: "logEnable", type: "bool", title: "Enable debug logging", defaultValue: true
    }
}

def logsOff() {
    log.warn "debug logging disabled..."
    device.updateSetting("logEnable", [value: "false", type: "bool"])
}

def updated() {
    log.info "updated..."
    log.warn "debug logging is: ${logEnable == true}"
    if (logEnable) runIn(1800, logsOff)
}

def parse(String description) {
    if (logEnable) log.debug(description)
}

def on() {
    preset("sun");
}

def off() {
    preset("off");
}

def preset(String name) {
    String url = "http://" + settings.ip + "/preset?name=" + name;
    if (logEnable) log.debug "Sending off GET request to [${url}]"

    try {
        httpGet(url) { resp ->
            if (resp.success) {
                sendEvent(name: "switch", value: name == "off" ? name : "on", isStateChange: true)
            }
            if (logEnable)
                if (resp.data) log.debug "${resp.data}"
        }
    } catch (Exception e) {
        log.warn "Call to off failed: ${e.message}"
    }
}
