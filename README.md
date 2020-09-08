# OpenNMS BigPanda Plugin

## Usage

Download the plugin's .kar file into your OpenNMS deploy directory i.e.:
```
sudo wget https://github.com/OpenNMS/opennms-bigpanda-plugin/releases/download/alpha1/opennms-bigpanda-plugin.kar -P /opt/opennms/deploy/
```

Configure the plugin to be installed when OpenNMS starts:
```
echo 'opennms-plugins-bigpanda wait-for-kar=opennms-bigpanda-plugin' | sudo tee /opt/opennms/etc/featuresBoot.d/bigpanda.boot
```

Access the [Karaf shell](https://opennms.discourse.group/t/karaf-cli-cheat-sheet/149) and install the feature manually to avoid having to restart:
```
feature:install opennms-plugins-bigpanda
```

Configure settings:
```
config:edit org.opennms.integrations.bigpanda
property-set token YOUR-TOKEN-HERE
config:update
```

Verify your setup:
```
opennms:health-check
```

View alert/event statistics:
```
opennms-bigpanda:stats
```

## Building

Build and install the plugin into your local Maven repository using:
```
mvn clean install
```

> OpenNMS normally runs as root, so make sure the artifacts are installed in `/root/.m2` or try making `/root/.m2` symlink to your user's repository

From the OpenNMS Karaf shell:
```
feature:repo-add mvn:org.opennms.plugins/karaf-features/1.0.0-SNAPSHOT/xml
feature:install opennms-plugins-bigpanda
```

Configure your API key:
```
config:edit org.opennms.integrations.bigpanda
property-set token YOUR-TOKEN-HERE
config:update
```

Alarms should now be forwarded as alerts to BigPanda.

You can use the following shell command to see statistics about how many alerts were forwarded:
```
opennms-bigpanda:stats
```

You can also try forwarding the topology using:
```
opennms-bigpanda:push-topology
```
