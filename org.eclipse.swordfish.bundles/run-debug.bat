cd target/bundles
java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000 -jar org.eclipse.osgi-3.4.2.R34x_v20080826-1230.jar -console -clean
