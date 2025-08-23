import { CaretLeft, PencilSimple } from "phosphor-react";
import Header from "../components/header";
import { useNavigate } from "react-router-dom";
import { useState } from "react";
import SubjectForm from "../components/subjectForm";

interface Subject {
  id: number;
  name: string;
  frontLabel: string;
  backLabel: string;
}

const SubjectsPage = () => {
  const navigate = useNavigate();

  const [subjects, setSubjects] = useState<Subject[]>([
    { id: 1, name: "German", frontLabel: "English", backLabel: "Deutsch" },
    { id: 2, name: "Spanish", frontLabel: "English", backLabel: "Español" },
  ]);

  const [currentSubjectId, setCurrentSubjectId] = useState<number>(1);
  const [editSubjectId, setEditSubjectId] = useState<number | null>(null);

  const handleSelectSubject = (id: number) => {
    setCurrentSubjectId(id);
    console.log("Selected subject:", id);
  };

  const handleSaveEdit = (
    id: number,
    updated: { name: string; frontLabel: string; backLabel: string }
  ) => {
    setSubjects((prev) =>
      prev.map((s) => (s.id === id ? { ...s, ...updated } : s))
    );
    setEditSubjectId(null);
    console.log("Saved subject:", id, updated);
  };

  const handleAddSubject = (newSub: {
    name: string;
    frontLabel: string;
    backLabel: string;
  }) => {
    const newId = subjects.length + 1;
    const subject: Subject = { id: newId, ...newSub };
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
            <div className="absolute left-0">
              <button
                id="decks-back-button"
                className="cursor-pointer"
                onClick={() => navigate("/decks")}
              >
                <CaretLeft size={24} />
              </button>
            </div>
            <h1 className="text-xl font-bold text-center mx-auto">Subjects</h1>
          </div>

          {/* Subjects list */}
          <ul className="space-y-3">
            {subjects.map((subject) => {
              const isActive = subject.id === currentSubjectId;
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
                      initialValues={subject}
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
                        className="p-2 border-white border-2 hover:border-black hover:bg-yellow-200 rounded hover:cursor-pointer"
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
