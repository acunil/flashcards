import { CaretLeft, PencilSimple } from "phosphor-react";
import Header from "../components/header";
import { useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import SubjectForm from "../components/subjectForm";
import { useAppContext } from "../contexts";
import useUpdateSubject from "../hooks/subjects/useUpdateSubject";

const SubjectsPage = () => {
  const navigate = useNavigate();
  const { subjects, selectedSubjectId, setSelectedSubjectId, setSubjects } =
    useAppContext();

  const [editSubjectId, setEditSubjectId] = useState<number | null>(null);

  const { updateSubject } = useUpdateSubject();

  const handleSelectSubject = (id: number) => {
    setSelectedSubjectId(id);
  };

  useEffect(() => {
    // If no subject is selected and there are subjects, select the first one
    if (subjects.length > 0 && !selectedSubjectId) {
      setSelectedSubjectId(subjects[0].id);
    }
  }, [subjects, selectedSubjectId, setSelectedSubjectId]);

  const handleSaveEdit = async (
    id: number,
    updated: { name: string; frontLabel: string; backLabel: string }
  ) => {
    try {
      await updateSubject({
        id,
        frontLabel: updated.frontLabel,
        backLabel: updated.backLabel,
      });

      // Update local state after successful API call
      setSubjects((prev) =>
        prev.map((s) => (s.id === id ? { ...s, ...updated } : s))
      );

      setEditSubjectId(null);
      console.log("Saved subject:", id, updated);
    } catch (err) {
      console.error("Failed to update subject:", err);
    }
  };

  const handleAddSubject = (newSub: {
    name: string;
    frontLabel: string;
    backLabel: string;
  }) => {
    const newId =
      subjects.length > 0 ? Math.max(...subjects.map((s) => s.id)) + 1 : 1;
    const subject = { id: newId, ...newSub };
    setSubjects((prev) => [...prev, subject]);
    console.log("Added subject:", subject);
  };

  return (
    <div className="bg-yellow-200 min-h-screen">
      <Header />
      <div className="flex justify-center">
        <div className="bg-white w-full max-w-screen-sm border-black border-2 p-4 rounded m-4">
          {/* Header with Back Button */}
          <div className="relative flex items-center h-12 mb-4">
            {subjects.length > 0 && (
              <div className="absolute left-0">
                <button
                  id="decks-back-button"
                  className="cursor-pointer"
                  onClick={() => navigate(-1)}
                >
                  <CaretLeft size={24} />
                </button>
              </div>
            )}
            <h1 className="text-xl font-bold text-center mx-auto">Subjects</h1>
          </div>
          {subjects.length === 0 && (
            <p className="text-center">Create a subject to begin studying</p>
          )}

          {/* Subjects list */}
          <ul className="space-y-3">
            {subjects.map((subject) => {
              const isActive = subject.id === selectedSubjectId;
              const isEditing = editSubjectId === subject.id;

              return (
                <li
                  key={subject.id}
                  className="flex flex-col border-2 p-3 rounded cursor-pointer hover:bg-gray-50"
                  onClick={() => !isEditing && handleSelectSubject(subject.id)}
                >
                  {isEditing ? (
                    <SubjectForm
                      mode="edit"
                      subject={subject}
                      onSave={(values) => handleSaveEdit(subject.id, values)}
                      onCancel={() => setEditSubjectId(null)}
                    />
                  ) : (
                    <div className="flex items-center justify-between">
                      {/* Radio indicator + labels */}
                      <div className="flex items-center gap-3">
                        <div className="w-5 h-5 rounded-full border-2 flex items-center justify-center">
                          {isActive && (
                            <div className="w-3 h-3 bg-black rounded-full"></div>
                          )}
                        </div>
                        <div>
                          <p className="font-semibold">{subject.name}</p>
                          <p className="text-xs text-gray-500">
                            {subject.frontLabel} | {subject.backLabel}
                          </p>
                        </div>
                      </div>

                      {/* Edit button */}
                      <button
                        onClick={(e) => {
                          e.stopPropagation();
                          setEditSubjectId(subject.id);
                        }}
                        className="p-2 border-inherit border-2 hover:border-black hover:bg-yellow-200 rounded hover:cursor-pointer"
                      >
                        <PencilSimple size={23} />
                      </button>
                    </div>
                  )}
                </li>
              );
            })}
          </ul>

          {/* Add new subject */}
          <div className="mt-6 border-t-2 pt-4">
            <h2 className="text-lg font-bold mb-2">Add New Subject</h2>
            <SubjectForm mode="add" onSave={handleAddSubject} />
          </div>
        </div>
      </div>
    </div>
  );
};

export default SubjectsPage;
