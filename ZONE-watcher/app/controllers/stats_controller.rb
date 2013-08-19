#!/usr/bin/env ruby
require 'rubygems'
require 'stathat'

class StatsController < ApplicationController
  # GET /stats
  # GET /stats.json
  def index

    @stats = Stat.all

    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @stats }
    end
  end
end
