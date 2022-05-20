#!/usr/bin/env groovy

// Credits to https://stackoverflow.com/a/42068552 🙏

import jenkins.model.Jenkins
import hudson.slaves.SlaveComputer
import hudson.slaves.DumbSlave
import hudson.plugins.sshslaves.SSHLauncher

String call() {

  def computer = Jenkins.getInstance().getComputer(env.NODE_NAME)

  if (!(computer instanceof SlaveComputer)) {
      error('Not an ordinary slave')
  }

  def node = computer.getNode()
  if (!(node instanceof DumbSlave)) {
      error('Notn a dumb slave')
  }

  def launcher = node.getLauncher()
  if (!(launcher instanceof SSHLauncher)) {
      error('Not an SSHLauncher')
  }

  return launcher.getHost()
}