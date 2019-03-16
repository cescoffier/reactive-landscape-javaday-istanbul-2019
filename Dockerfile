FROM registry.fedoraproject.org/fedora-minimal
WORKDIR /work/
ENV CONTAINERIZED=true
COPY target/*-runner /work/application
COPY src/main/resources/ddl.sql /work/ddl.sql
COPY src/main/resources/webroot /work/webroot
RUN chmod 775 /work
EXPOSE 8080
CMD ["./application", "-Dquarkus.http.host=0.0.0.0"]