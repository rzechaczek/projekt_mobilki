package com.cookbook.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cookbook.app.R;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {

    private List<String> ingredients = new ArrayList<>();
    private List<String> measures = new ArrayList<>();
    private float scaleFactor = 1.0f;

    public void setData(List<String> ingredients, List<String> measures) {
        this.ingredients = ingredients != null ? ingredients : new ArrayList<>();
        this.measures = measures != null ? measures : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setScaleFactor(float scale) {
        this.scaleFactor = scale;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ingredient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textIngredient.setText(ingredients.get(position));
        String measure = position < measures.size() ? measures.get(position) : "";
        holder.textMeasure.setText(scaleMeasure(measure));
    }

    private String scaleMeasure(String measure) {
        if (measure == null || measure.isEmpty() || scaleFactor == 1.0f) {
            return measure;
        }
        Pattern pattern = Pattern.compile("^(\\d+(?:[./]\\d+)?)(.*)$");
        Matcher matcher = pattern.matcher(measure.trim());
        if (matcher.find()) {
            String numberPart = matcher.group(1);
            String rest = matcher.group(2);
            try {
                double value;
                if (numberPart.contains("/")) {
                    String[] parts = numberPart.split("/");
                    value = Double.parseDouble(parts[0]) / Double.parseDouble(parts[1]);
                } else {
                    value = Double.parseDouble(numberPart);
                }
                double scaled = value * scaleFactor;
                String formatted;
                if (scaled == Math.floor(scaled)) {
                    formatted = String.valueOf((int) scaled);
                } else {
                    formatted = String.format("%.1f", scaled);
                }
                return formatted + rest;
            } catch (NumberFormatException e) {
                return measure;
            }
        }
        return measure;
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textIngredient;
        TextView textMeasure;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textIngredient = itemView.findViewById(R.id.text_ingredient_name);
            textMeasure = itemView.findViewById(R.id.text_ingredient_measure);
        }
    }
}