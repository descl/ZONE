class CreateSearchFilters < ActiveRecord::Migration
  def change
    create_table :search_filters do |t|
      t.string :value
      t.string :kind
      t.belongs_to :search

      t.timestamps
    end
  end
end
