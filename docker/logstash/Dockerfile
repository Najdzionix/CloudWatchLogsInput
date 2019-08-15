FROM docker.elastic.co/logstash/logstash:7.3.0

#COPY plugin/logstash-input-java_input_example-1.0.0.gem /usr/share/logstash/plugin/logstash-input-java_input_example-1.0.0.gem
COPY plugin/*.gem /plugin/my.gem
RUN bin/logstash-plugin install --no-verify --local  /plugin/my.gem

COPY logstash.conf /usr/share/logstash/pipeline/logstash.conf


# Add your logstash plugins setup here
# Example: RUN logstash-plugin install logstash-filter-json
