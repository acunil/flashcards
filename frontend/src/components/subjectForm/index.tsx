// SubjectForm.tsx
import { Check, Plus } from "phosphor-react";
import { useEffect, useState } from "react";
import type { Subject } from "../../types/subject";

interface SubjectFormProps {
  subject?: Subject;
  mode: "edit" | "add";
  onSave: (values: {
    name: string;
    frontLabel: string;
    backLabel: string;
  }) => void;
  onCancel?: () => void;
}

const SubjectForm = ({ subject, mode, onSave, onCancel }: SubjectFormProps) => {
  const [values, setValues] = useState({
    name: "",
    frontLabel: "",
    backLabel: "",
  });

  const [errors, setErrors] = useState<{ name?: string }>({});

  useEffect(() => {
    if (subject) {
      setValues({
        name: subject?.name,
        frontLabel: subject?.frontLabel || "",
        backLabel: subject?.backLabel || "",
      });
    }
  }, [subject]);

  const handleChange = (field: keyof typeof values, value: string) => {
    setValues((prev) => ({ ...prev, [field]: value }));

    // clear error while typing
    if (field === "name" && value.trim() !== "") {
      setErrors((prev) => ({ ...prev, name: undefined }));
    }
  };

  const handleSubmit = (e?: React.FormEvent) => {
    if (e) e.preventDefault();

    const newErrors: typeof errors = {};
    if (!values.name.trim()) {
      newErrors.name = "Name is required.";
    }

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    onSave(values);
  };

  return (
    <form className="flex flex-col gap-2" onSubmit={handleSubmit}>
      <div className="flex flex-col mb-2">
        <label className="text-xs">
          Name <span className="text-red-500">*</span>
        </label>
        <input
          type="text"
          value={values.name}
          onChange={(e) => handleChange("name", e.target.value)}
          className={`border p-1 rounded text-sm ${
            errors.name ? "border-red-500" : "border-gray-500"
          }`}
          placeholder="Enter text..."
        />
        {errors.name && (
          <span className="text-xs text-red-500 mt-1">{errors.name}</span>
        )}
      </div>
      <div className="flex flex-col sm:flex-row gap-3">
        <div className="flex flex-col sm:w-1/2 w-full">
          <label className="text-xs">Front label</label>
          <input
            type="text"
            value={values.frontLabel}
            onChange={(e) => handleChange("frontLabel", e.target.value)}
            className="border p-1 rounded w-full text-sm border-gray-500"
            placeholder="Enter text..."
          />
        </div>
        <div className="flex flex-col sm:w-1/2 w-full">
          <label className="text-xs">Back label</label>
          <input
            type="text"
            value={values.backLabel}
            onChange={(e) => handleChange("backLabel", e.target.value)}
            className="border p-1 rounded w-full text-sm border-gray-500"
            placeholder="Enter text..."
          />
        </div>
      </div>

      <div className="flex justify-end gap-2 mt-2">
        {onCancel && (
          <button
            type="button"
            onClick={onCancel}
            className="px-3 py-1 border-2 cursor-pointer text-sm rounded hover:bg-gray-100"
          >
            Cancel
          </button>
        )}
        <button
          type="submit"
          className="flex items-center gap-1 px-3 py-1 border-2 border-black rounded text-sm bg-green-200 hover:bg-green-300 hover:cursor-pointer"
        >
          {mode === "edit" ? <Check size={16} /> : <Plus size={16} />}
          {mode === "edit" ? "Save" : "Add"}
        </button>
      </div>
    </form>
  );
};

export default SubjectForm;
