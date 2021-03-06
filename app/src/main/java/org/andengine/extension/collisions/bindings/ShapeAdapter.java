package org.andengine.extension.collisions.bindings;

import org.andengine.entity.shape.IShape;

import br.com.makersf.frameworks.shared.collisioncore.pixelperfect.Transformation;


public class ShapeAdapter implements br.com.makersf.frameworks.shared.collisioncore.pixelperfect.IShape {

	private final IShape mShape;

	public ShapeAdapter(IShape pShape) {
		mShape = pShape;
	}

	@Override
	public Transformation getLocalToSceneTransformation() {
		return TransformationAdapter.adapt(mShape.getLocalToSceneTransformation());
	}

	@Override
	public Transformation getSceneToLocalTransformation() {
		return TransformationAdapter.adapt(mShape.getSceneToLocalTransformation());
	}

	@Override
	public float getWidth() {
		return mShape.getX();
	}

	@Override
	public float getHeight() {
		return mShape.getY();
	}

}
